const { merge } = require("webpack-merge");
const common = require("./webpack.base");
const { BundleAnalyzerPlugin } = require("webpack-bundle-analyzer");
const TerserPlugin = require("terser-webpack-plugin");
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
const CompressionWebpackPlugin = require("compression-webpack-plugin");

module.exports = merge(common, {
  mode: "production",
  plugins: [
    new CompressionWebpackPlugin({
      algorithm: "gzip",
      test: /\.(js|css)$/,
    }),
    new BundleAnalyzerPlugin({
      analyzerMode: "static",
      openAnalyzer: false
    })
  ],
  optimization: {
    minimize: true,
    minimizer: [
      `...`,
      new TerserPlugin({
        terserOptions: {
          compress: {
            drop_console: true
          }
        }
      }),
      new CssMinimizerPlugin()
    ],
    splitChunks: {
      cacheGroups: {
        react: {
          test: /[\\/]node_modules[\\/](react|react-dom|react-redux|redux|redux-persist|localforage|axios)[\\/]/,
          name: "react",
          chunks: "all"
        }
      }
    }
  }
});
