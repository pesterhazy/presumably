const util = require("util");
const execFile = util.promisify(require("child_process").execFile);
const rimraf = util.promisify(require("rimraf"));
const mkdir = require("fs").promises.mkdir;
const readFile = require("fs").promises.readFile;
const existsSync = require("fs").existsSync;
import matter = require("gray-matter");
const slugify = require("slugify");
const fg = require("fast-glob");
const flatMap = require("array.prototype.flatmap");

// FIXME: filter out unpublished posts
// FIXME: create index page
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
    ...flatMap(Object.entries(meta), (k: string, v: string) => [
      "--metadata",
      k + "=" + v
    ]),
    inFile
  ]);
}

async function analyze(inFile: string) {
  let s = await readFile(inFile, "utf-8");
  let { data } = matter(s);
  let { title } = data;

  return {
    title,
    date: data["date-published"],
    slug: slugify(data.title, { lower: true })
  };
}

async function run() {
  try {
    let inputs = await fg(["posts/*.md"]);
    console.log("" + inputs.length + " inputs found");
    await init("out");
    await staticFiles("resources/public", "out");
    for (let input of inputs) {
      console.log(input);
      let data = await analyze(input);
      let outFile = "out/" + data.slug + ".html";
      // moment(new Date()).format("DD MMM YYYY")
      await transform(input, outFile, { "date-formatted": "asdf" });
    }
    console.log("All done.");
  } catch (e) {
    console.error("Failed\n" + e.stack);
    process.exit(1);
  }
}

run();
