const merge = require("webpack-merge");
const common = require("./webpack.base");

module.exports = merge(common, {
  devtool: "cheap-module-eval-source-map",
  devServer: {
    contentBase: "dist/",
    host: "0.0.0.0",
    disableHostCheck: true,
    hot: true,
    inline: true
  }
});
