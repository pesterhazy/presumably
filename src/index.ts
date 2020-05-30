const util = require("util");
const execFile = util.promisify(require("child_process").execFile);

async function run() {
  let { stdout } = await execFile("node", ["--version"]);

  console.log(stdout);
}

run();
