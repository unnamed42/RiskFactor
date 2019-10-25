const TerserPlugin = require('terser-webpack-plugin');

const config = {
  module: {
    rules: [{
      test: /\.[tj]sx?$/,
      exclude: /node_modules/,
      use: ["babel-loader"]
    }, {
      enforce: "pre",
      test: /\.js$/,
      loader: "source-map-loader"
    }]
  }
};

if (process.env.NODE_ENV === "production")
  config.optimization = {
    minimize: true,
    minimizer: [new TerserPlugin()]
  };

module.exports = config;
