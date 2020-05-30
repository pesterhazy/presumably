const util = require("util");
const execFile = util.promisify(require("child_process").execFile);

async function transform() {
  await execFile("pandoc", [
    "--output=out/index.html",
    "--to=html5",
    "--data-dir",
    ".",
    "--template",
    "presumably.html5", // FIXME
    "posts/monorepos.md"
  ]);
  console.log("ok");
}

async function run() {
  try {
    await transform();
  } catch (e) {
    console.error("Failed\n" + e.stack);
    process.exit(1);
  }
}

run();
