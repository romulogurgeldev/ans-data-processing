const { defineConfig } = require('@vue/cli-service');
const webpack = require('webpack'); // Adicione esta linha para importar o webpack

module.exports = defineConfig({
  transpileDependencies: true,
  
  devServer: {
    headers: {
      "Content-Security-Policy": "script-src 'self' 'unsafe-eval';"
    },
    port: 8081,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: { '^/api': '' },
        secure: false
      }
    },
    client: {
      overlay: {
        warnings: true,
        errors: true
      }
    },
  },

  configureWebpack: {
    devtool: 'nosources-source-map',
    performance: {
      hints: process.env.NODE_ENV === 'production' ? 'warning' : false
    },
    optimization: {
      splitChunks: {
        chunks: 'all',
        cacheGroups: {
          vendors: {
            test: /[\\/]node_modules[\\/]/,
            priority: -10
          },
          default: {
            minChunks: 2,
            priority: -20,
            reuseExistingChunk: true
          }
        }
      }
    },
    plugins: [
      new webpack.DefinePlugin({
        __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: JSON.stringify(false),
        __VUE_OPTIONS_API__: JSON.stringify(true),
        __VUE_PROD_DEVTOOLS__: JSON.stringify(false),
        // Adicione outras variáveis globais se necessário
      })
    ]
  },

  css: {
    loaderOptions: {
      css: {
        sourceMap: true
      },
      scss: {
        additionalData: `@import "@/assets/styles/variables.scss";`
      }
    }
  },

  pluginOptions: {
    i18n: {
      locale: 'pt-BR',
      fallbackLocale: 'pt-BR',
      localeDir: 'locales',
      enableInSFC: false
    }
  },

  chainWebpack: config => {
    config.devtool('nosources-source-map');
    
    // Configuração para imagens
    config.module
      .rule('images')
      .test(/\.(png|jpe?g|gif|webp)(\?.*)?$/)
      .use('url-loader')
      .loader('url-loader')
      .tap(options => ({
        ...options,
        limit: 4096,
        fallback: {
          loader: 'file-loader',
          options: {
            name: 'img/[name].[hash:8].[ext]',
            esModule: false
          }
        }
      }));
    
    // Configuração para SVG
    config.module
      .rule('svg')
      .test(/\.(svg)(\?.*)?$/)
      .use('file-loader')
      .loader('file-loader')
      .options({
        name: 'img/[name].[hash:8].[ext]'
      });
  }
});