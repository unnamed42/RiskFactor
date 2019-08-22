const merge = require("webpack-merge");
const common = require("./webpack.common");
const WebpackCdnPlugin = require("webpack-cdn-plugin");

module.exports = merge(common, {
  mode: "production",
  plugins: [
    new WebpackCdnPlugin({
      modules: [{
        name: "react-dom",
        var: "ReactDOM",
        path: "cjs/react-dom.production.min.js"
      }, {
        name: "react",
        var: "React",
        path: "cjs/react.production.min.js"
      }]
    })
  ]
});
