const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");

module.exports = {
  plugins: [
    new MiniCssExtractPlugin({
      filename: "[name].[hash:5].css",
      chunkFilename: "[name].[hash:5].css",
      ignoreOrder: true,
    })
  ],
  module: {
    rules: [{
      test: /\.(le|c)ss$/,
      use: [
        {
          loader: MiniCssExtractPlugin.loader,
          options: {
            hmr: process.env.NODE_ENV === "development"
          },
        },
        "css-loader",
        {
          loader: "less-loader",
          options: { javascriptEnabled: true }
        }
      ]
    }]
  },
  optimization: {
    minimizer: [
      new OptimizeCSSAssetsPlugin({})
    ]
  }
};
