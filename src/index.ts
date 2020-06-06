const util = require("util");
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
import moment = require("moment");
import hiccup = require("@thi.ng/hiccup");

// FIXME: create index page
// FIXME: google analytics
// FIXME: make sure that slugs match

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

async function transform(
  inFile: string,
  outFile: string,
  meta: Record<string, string>
) {
  await execFile("pandoc", [
    "--output",
    outFile,
    "--to=html5",
    "--data-dir",
    ".",
    "--template",
    "presumably.html",
    ...flatMap(Object.entries(meta), ([k, v]: [string, string]) => [
      "--metadata",
      k + "=" + v
    ]),
    inFile
  ]);
}

async function analyze(inFile: string) {
  let s = await readFile(inFile, "utf-8");
  let { data } = matter(s);
  let fullTitle = data.title;
  if (data.subtitle) fullTitle += " " + data.subtitle;

  return {
    fullTitle: fullTitle,
    date: data["date-published"],
    slug: slug(fullTitle)
  };
}

interface AnalysisData {
  fullTitle: string;
}

interface TocEntry {
  fileName: string;
  analysisData: AnalysisData;
}

async function toc(contents: TocEntry[], outFile: string) {
  let div = [
    "div",
    ["h2", "Contents"],
    ...contents.map(entry => [
      "div",
      ["a", { href: "/" + entry.fileName }, entry.analysisData.fullTitle]
    ])
  ];
  let data = template({
    body: hiccup.serialize(div),
    title: "FIXME",
    subtitle: "FIXME",
    date: "FIXME"
  });
  await writeFile(outFile, hiccup.serialize(data));
}

async function run() {
  try {
    let inputs = await fg(["posts/*.md"]);
    let result = [];
    console.log("" + inputs.length + " inputs found");
    await init("out");
    await staticFiles("resources/public", "out");
    for (let input of inputs) {
      console.log(input);
      let data = await analyze(input);
      if (!data.date) {
        console.log("Skipping unpublished");
        continue;
      }
      let fileName = data.slug + ".html";
      let outFile = "out/" + fileName;
      console.log("=> " + outFile);
      let meta: Record<string, string> = {};
      if (data.date) {
        meta.date = moment(data.date).format("DD MMM YYYY");
      }
      await transform(input, outFile, meta);
      result.push({ analysisData: data, fileName });
    }
    toc(result, "out/index.html");
    console.log("=> " + "out/index.html");
    console.log("All done.");
  } catch (e) {
    console.error("Failed\n" + e.stack);
    process.exit(1);
  }
}

run();

interface TemplateParams {
  body: string;
  title: string;
  subtitle: string;
  date: string;
}

function template({ body, title, subtitle, date }: TemplateParams) {
  return [
    "html",
    [
      "head",
      ["meta", { charset: "utf-8" }],
      ["title", title],
      ["link", { rel: "stylesheet", href: "/css/style.css" }],
      ["link", { rel: "stylesheet", href: "/vendor/basscss@8.0.1.min.css" }],
      ["link", { rel: "stylesheet", href: "/vendor/highlight.css" }],
      [
        "link",
        {
          rel: "stylesheet",
          href: "https://fonts.googleapis.com/css?family=Josefin+Sans"
        }
      ]
    ],
    [
      "body",
      [
        "div.content.mx-auto",
        [
          "div.clearfix",
          [
            "div.header",
            ["a", { href: "/" }, "presumably for side-effects"],
            ["br"],
            "a blog about programming"
          ],
          ["div", body]
        ],
        ["hr.rule"],
        [
          "p.m2",
          "This is",
          ["i", "presumably for side-effects"],
          ", a blog by Paulus Esterhazy. Don't forget to say hello ",
          ["a", { href: "https://twitter.com/pesterhazy" }, "on twitter"],
          " or ",
          ["a", { href: "mailto:pesterhazy@gmail.com" }, "by email"]
        ]
      ]
    ]
  ];
}
