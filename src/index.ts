const util = require("util");
const execFile = util.promisify(require("child_process").execFile);

async function run() {
  await execFile("pandoc", [
    "--output=out/index.html",
    "--to=html5",
    "--data-dir",
    ".",
    "--template",
    "presumably.html5",
    "posts/monorepos.md"
  ]);
  console.log("ok");
}

run();
