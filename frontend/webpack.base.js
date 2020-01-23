const { join, resolve } = require("path");

const HtmlWebpackPlugin = require("html-webpack-plugin");
const {CleanWebpackPlugin} = require("clean-webpack-plugin");
const FriendlyErrorsWebpackPlugin = require("friendly-errors-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");

const root = resolve("./");

module.exports = {
  mode: process.env.NODE_ENV,
  stats: {children: false},
  entry: [join(root, "/src/index")],
  output: {
    path: join(root, "/dist"),
    filename: "[name].[hash:5].js",
    chunkFilename: "[name].[hash:5].js",
    publicPath: "/"
  },
  resolve: {
    extensions: [".ts", ".tsx", ".js", ".jsx"],
    alias: {
      "@": join(root, "/src")
    }
  },
  module: {
    rules: [
      {
        sideEffects: false
      },
      {
        test: /\.[tj]sx?$/,
        exclude: /node_modules/,
        use: ["babel-loader"]
      }, {
        enforce: "pre",
        test: /\.js$/,
        loader: "source-map-loader"
      },
      {
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
            options: {javascriptEnabled: true}
          }
        ]
      },
      {
        test: /\.(png|svg|jpe?g|gif)$/,
        use: ["file-loader"]
      },
      {
        test: /\.(woff2?|eot|ttf|otf)$/,
        use: ["file-loader"]
      }
    ]
  },
  plugins: [
    new CleanWebpackPlugin(),
    new HtmlWebpackPlugin({
      template: `${root}/src/index.html`
    }),
    new FriendlyErrorsWebpackPlugin(),
    new MiniCssExtractPlugin({
      filename: "[name].[hash:5].css",
      chunkFilename: "[name].[hash:5].css",
      ignoreOrder: true,
    })
  ],
  optimization: {
    // runtimeChunk: "single",
    minimizer: [new OptimizeCSSAssetsPlugin({})],
    moduleIds: "hashed",
    splitChunks: {
      cacheGroups: {
        default: false,
        vendor: {
          name: "vendor",
          chunks: "initial",
          test: /node_modules/,
          priority: 20
        },
        common: {
          name: "common",
          minChunks: 2,
          chunks: "async",
          priority: 10,
          reuseExistingChunk: true,
          enforce: true
        }
      }
    }
  }
};

// function envKeys() {
//     const defaultEnv = join(__dirname, "../../.env");
//     const envFile = `${defaultEnv}.${process.env.NODE_ENV}`;
//     // fallback to .env if .env.production | .env.development not found
//     const envPath = existsSync(envFile) ? envFile :
//         existsSync(defaultEnv) ? defaultEnv : null;
//     const keys = envPath ? config({ path: envPath }).parsed : {};
//     return Object.keys(keys).reduce((obj, key) => {
//         obj[`process.env.${key}`] = JSON.stringify(keys[key]);
//         return obj;
//     }, {});
// }
//
// module.exports = {
//     plugins: [
//         new DefinePlugin(envKeys())
//     ]
// };
