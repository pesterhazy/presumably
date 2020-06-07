import util = require("util");
const execFile = util.promisify(require("child_process").execFile);
const rimraf = util.promisify(require("rimraf"));
const mkdir = require("fs").promises.mkdir;
const readFile = require("fs").promises.readFile;
const writeFile = require("fs").promises.writeFile;
const existsSync = require("fs").existsSync;
import matter = require("gray-matter");
const slug = require("slug");
const fg = require("fast-glob");
const flatMap = require("array.prototype.flatmap");
import moment from "moment";
import hiccup = require("@thi.ng/hiccup");
import { Feed } from "feed";

import { template } from "./template";

// FIXME: redirects

// ********************************************************************

const blogTitle = "Presumably for side-effects";
const baseUrl = "https://presumably.de";

// ********************************************************************

interface AnalysisData {
  fullTitle: string;
  title: string;
  subtitle: string;
  date: Date;
  slug: string;
}

interface Post {
  fileName: string;
  html: string;
  analysisData: AnalysisData;
}

// ********************************************************************

const formatDate = (date: Date) => moment(date).format("MMM DD, YYYY");

async function init(outDir: string) {
  await rimraf(outDir);
  await mkdir(outDir);
}

async function staticFiles(publicDir: string, outDir: string) {
  if (!existsSync(publicDir)) {
    throw new Error("Directory not found: " + publicDir);
  }
  await execFile("rsync", ["-va", "resources/public/", outDir]);
}

async function transform(inFile: string, meta: Record<string, string>) {
  let result = await execFile("pandoc", [
    "--to=html5",
    ...flatMap(Object.entries(meta), ([k, v]: [string, string]) => [
      "--metadata",
      k + "=" + v
    ]),
    inFile
  ]);
  return result.stdout;
}

async function analyze(inFile: string): Promise<AnalysisData> {
  let s = await readFile(inFile, "utf-8");
  let { data } = matter(s);
  let fullTitle = data.title;
  if (data.subtitle) fullTitle += ". " + data.subtitle;

  return {
    fullTitle: fullTitle,
    title: data.title,
    subtitle: data.subtitle,
    date: data["date-published"],
    slug: slug(fullTitle)
  };
}

// ********************************************************************

async function writeToc(contents: Post[], outFile: string) {
  let div = [
    "div",
    ["h2", "Contents"],
    ...[...contents]
      .sort(
        (a: Post, b: Post) =>
          b.analysisData.date.getTime() - a.analysisData.date.getTime()
      )
      .map(entry => [
        "div",
        ["a", { href: "/" + entry.fileName }, entry.analysisData.fullTitle],
        " (" + formatDate(entry.analysisData.date) + ")"
      ])
  ];
  let data = template({
    body: hiccup.serialize(div),
    title: blogTitle
  });
  await writeFile(outFile, hiccup.serialize(data));
}

async function writeArticle(post: Post, outFile: string) {
  let body = [
    "div",
    [
      "header#title-block-header",
      ["h1.title", post.analysisData.title],
      ["h2.subtitle", post.analysisData.subtitle],
      ["p.date", "published " + formatDate(post.analysisData.date)]
    ],
    post.html
  ];
  let data = template({
    body: hiccup.serialize(body),
    title: post.analysisData.title
  });
  await writeFile(outFile, hiccup.serialize(data));
}

async function writeFeed(entries: Post[], outFile: string) {
  const feed = new Feed({
    title: blogTitle,
    description: "This is my personal feed!",
    id: "https://presumably.de",
    link: "https://presumably.de",
    copyright: "Copyright Paulus Esterhazy",
    language: "en",
    feedLinks: {
      atom: "https://presumably.de/atom.xml"
    },
    author: {
      name: "Paulus Esterhazy"
    }
  });

  entries.forEach(post => {
    feed.addItem({
      title: post.analysisData.fullTitle,
      id: baseUrl + "/" + post.fileName,
      link: baseUrl + "/" + post.fileName,
      date: post.analysisData.date
    });
  });

  await writeFile(outFile, feed.atom1());
}

// ********************************************************************

async function run() {
  try {
    let inputs = await fg(["posts/*.md"]);
    let result: Post[] = [];
    console.log("" + inputs.length + " inputs found");
    await init("out");
    await staticFiles("resources/public", "out");
    for (let input of inputs) {
      console.log(input);
      let analysisData = await analyze(input);
      if (!analysisData.date) {
        console.log("Skipping unpublished");
        continue;
      }
      let fileName = analysisData.slug + ".html";
      let outFile = "out/" + fileName;
      console.log("=> " + outFile);
      let meta: Record<string, string> = {};
      if (analysisData.date) {
        meta.date = formatDate(analysisData.date);
      }
      let html = await transform(input, meta);
      let post = { analysisData: analysisData, fileName, html };
      result.push(post);
      writeArticle(post, outFile);
    }
    await writeToc(result, "out/index.html");
    console.log("=> " + "out/index.html");
    await writeFeed(result, "out/atom.xml");
    console.log("=> " + "out/atom.xml");
    console.log("All done.");
  } catch (e) {
    console.error("Failed\n" + e.stack);
    process.exit(1);
  }
}

run();
