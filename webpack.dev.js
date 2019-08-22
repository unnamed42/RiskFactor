const merge = require("webpack-merge");
const common = require("./scripts/webpack");

module.exports = merge(common, {
  mode: "development",
  devtool: "source-map"
});
