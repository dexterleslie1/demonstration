const { GlobalVariable, add } = require("./entry1-two")

const y = 200
const sum = add(GlobalVariable, y)

module.exports = {
    sum
}
