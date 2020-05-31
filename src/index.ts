const util = require("util");
const execFile = util.promisify(require("child_process").execFile);

// FIXME: add CSS
// FIXME: add "published"

async function transform(inFile: string, outFile: string) {
  await execFile("pandoc", [
    "--output",
    inFile,
    "--to=html5",
    "--data-dir",
    ".",
    "--template",
    outFile,
    "posts/monorepos.md"
  ]);
  console.log("ok");
}

async function run() {
  try {
    await transform("posts/monorepos.md", "out/index.html");
  } catch (e) {
    console.error("Failed\n" + e.stack);
    process.exit(1);
  }
}

run();
