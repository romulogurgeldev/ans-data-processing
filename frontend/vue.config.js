const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  
  // Configurações adicionais:
  devServer: {
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
    }
  },

  configureWebpack: {
    devtool: 'source-map', // Para melhor debugging
    performance: {
      hints: process.env.NODE_ENV === 'production' ? 'warning' : false
    },
    optimization: {
      splitChunks: {
        chunks: 'all'
      }
    }
  },

  css: {
    loaderOptions: {
      css: {
        sourceMap: true
      },
      scss: {
        additionalData: `@import "@/assets/styles/variables.scss";` // Se usar SCSS
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
    // Configuração para tratamento de imagens
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
            name: 'img/[name].[hash:8].[ext]'
          }
        }
      }))
  }
})