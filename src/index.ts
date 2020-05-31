const util = require("util");
const execFile = util.promisify(require("child_process").execFile);
const rimraf = util.promisify(require("rimraf"));
const mkdir = require("fs").promises.mkdir;
const existsSync = require("fs").existsSync;

// FIXME: add CSS
// FIXME: add "published"

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
    "--metadata",
    "title=XFIXME",
    inFile
  ]);
  console.log("ok");
}

async function run() {
  try {
    await init("out");
    await staticFiles("resources/public", "out");
    await transform("posts/monorepos.md", "out/index.html");
  } catch (e) {
    console.error("Failed\n" + e.stack);
    process.exit(1);
  }
}

run();
