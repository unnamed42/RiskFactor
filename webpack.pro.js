const merge = require("webpack-merge");
const common = require("./scripts/webpack");
// const WebpackCdnPlugin = require("webpack-cdn-plugin");
const { BundleAnalyzerPlugin } = require("webpack-bundle-analyzer");

module.exports = merge(common, {
  plugins: [
    // new WebpackCdnPlugin({
    //   modules: [{
    //     name: "react-dom",
    //     var: "ReactDOM",
    //     path: "cjs/react-dom.production.min.js"
    //   }]
    // }),
    // new BundleAnalyzerPlugin()
  ]
});
