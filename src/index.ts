const util = require("util");
const execFile = util.promisify(require("child_process").execFile);
const rimraf = util.promisify(require("rimraf"));
const mkdir = require("fs").promises.mkdir;
const readFile = require("fs").promises.readFile;
const existsSync = require("fs").existsSync;
import matter = require("gray-matter");
const slugify = require("slugify");
const fg = require("fast-glob");

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

async function transform(inFile: string, outFile: string) {
  await execFile("pandoc", [
    "--output",
    outFile,
    "--to=html5",
    "--data-dir",
    ".",
    "--template",
    "presumably.html",
    // "--metadata",
    // "title=XXX",
    inFile
  ]);
}

async function analyze(inFile: string) {
  let s = await readFile(inFile, "utf-8");
  let { data } = matter(s);

  return { slug: slugify(data.title, { lower: true }) };
}

async function run() {
  try {
    let inputs = await fg(["posts/*.md"]);
    console.log("" + inputs.length + " inputs found");
    await init("out");
    await staticFiles("resources/public", "out");
    for (let input of inputs) {
      console.log(input);
      let { slug } = await analyze(input);
      let outFile = "out/" + slug + ".html";
      await transform(input, outFile);
    }
    console.log("All done.");
  } catch (e) {
    console.error("Failed\n" + e.stack);
    process.exit(1);
  }
}

run();
