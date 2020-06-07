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

// FIXME: atom.xml
// FIXME: redirects

// ********************************************************************

const blogTitle = "Presumably for side-effects";

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

interface AnalysisData {
  fullTitle: string;
  title: string;
  subtitle: string;
  date: Date;
  slug: string;
}

interface TocEntry {
  fileName: string;
  html: string;
  analysisData: AnalysisData;
}

async function toc(contents: TocEntry[], outFile: string) {
  let div = [
    "div",
    ["h2", "Contents"],
    ...[...contents]
      .sort(
        (a: TocEntry, b: TocEntry) =>
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

async function article(entry: TocEntry, outFile: string) {
  let body = [
    "div",
    [
      "header#title-block-header",
      ["h1.title", entry.analysisData.title],
      ["h2.subtitle", entry.analysisData.subtitle],
      ["p.date", "published " + formatDate(entry.analysisData.date)]
    ],
    entry.html
  ];
  let data = template({
    body: hiccup.serialize(body),
    title: entry.analysisData.title
  });
  await writeFile(outFile, hiccup.serialize(data));
}

async function run() {
  try {
    let inputs = await fg(["posts/*.md"]);
    let result: TocEntry[] = [];
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
      let entry = { analysisData: analysisData, fileName, html };
      result.push(entry);
      article(entry, outFile);
    }
    toc(result, "out/index.html");
    console.log("=> " + "out/index.html");
    console.log("All done.");
  } catch (e) {
    console.error("Failed\n" + e.stack);
    process.exit(1);
  }
}

interface TemplateParams {
  body: string;
  title: string;
}

function template({ body, title }: TemplateParams) {
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
      ],
      "<script>hljs.initHighlightingOnLoad();</script><script>(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){\n  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),\n  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)\n  })(window,document,'script','https://www.google-analytics.com/analytics.js','ga');\n\n  ga('create', 'UA-81846608-1', 'auto');\n  ga('send', 'pageview');</script>",
      '<script src="/vendor/highlight.js" type="text/javascript"></script>',
      "<script> hljs.initHighlightingOnLoad(); </script>"
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
          "This is ",
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

run();
