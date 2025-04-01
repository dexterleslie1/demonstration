import *  as commander from "commander"

// 初始化commander
const program = new commander.Command()
    .showHelpAfterError()
    .allowUnknownOption(false)
    .allowExcessArguments(false);

program
    .name('fabric.js')
    .description('fabric.js DEV CLI tools')
    .version(process.env.npm_package_version)
    .showSuggestionAfterError();

program
    .command('sayhello')
    .description('Commander basic test for console.log hello world')
    .action(() => {
        console.log("Hello world!")
    });

program.parse(process.argv);