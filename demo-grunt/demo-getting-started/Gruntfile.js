module.exports = function(grunt) {
  // 项目配置
  grunt.initConfig({
    // 读取项目根目录下的 package.json 文件，并将其内容解析为JavaScript对象，存储在Grunt配置的 pkg 属性中。
    // 在任务配置中引用项目信息 ：可以通过模板语法 <%= pkg.xxx %> 在Grunt任务配置中引用package.json中的任何字段，如项目名称、版本号、描述等。
    pkg: grunt.file.readJSON('package.json'),
    
    // LESS编译配置
    // 任务名称 ： less - 对应 grunt-contrib-less 插件，用于将LESS预处理器语法转换为标准CSS
    less: {
      // 开发环境配置
      // 通过Grunt任务命令（如 grunt less:development 或 grunt less:production ）轻松切换编译模式
      development: {
        options: {
          // 不压缩CSS，保持代码格式便于开发和调试
          compress: false,
          // 生成 .css.map 源映射文件，方便在浏览器开发者工具中查看原始LESS代码行
          sourceMap: true
        },
        // 输出文件： dist/css/style.css （标准命名，不带压缩标识）
        // 每个环境都定义了输入输出关系： '输出文件路径': '输入文件路径'
        // 这里是将单个LESS文件编译为CSS，也支持多文件映射（如 {'a.css': 'a.less', 'b.css': 'b.less'} ）
        files: {
          'dist/css/style.css': 'src/less/style.less'
        }
      },
      // 生产环境配置
      production: {
        options: {
          // 压缩CSS，移除空格、注释等，减小文件大小以提高加载性能
          compress: true,
          // 不生成源映射文件，减少生产环境的文件数量
          sourceMap: false
        },
        // dist/css/style.min.css （带 .min 后缀，标识为压缩版本）
        files: {
          'dist/css/style.min.css': 'src/less/style.less'
        }
      }
    },
    
    // JS合并配置
    // 任务名称 ： concat - 对应 grunt-contrib-concat 插件，专门用于合并文本文件（主要是JavaScript和CSS）
    concat: {
      // 全局选项（options）
      options: {
        // 定义文件之间的分隔符为分号
        // 为什么需要分隔符 ：确保合并后的JavaScript代码语法正确。如果前一个文件的最后一行没有分号结束，后一个文件的第一行可能会与前一行合并导致语法错误
        separator: ';'
      },
      // 目标配置（dist）
      dist: {
        // 要合并的源文件路径数组
        // 文件按数组顺序合并： utils.js 在前， main.js 在后
        // 这样可以确保依赖关系正确（例如 main.js 可能依赖 utils.js 中的函数）
        src: ['src/js/utils.js', 'src/js/main.js'],
        // 合并后的输出文件路径
        // 输出到 dist/js/app.js ，成为一个统一的JavaScript文件
        dest: 'dist/js/app.js'
      }
    },
    
    // JS压缩配置
    // 任务名称 ： uglify - 对应 grunt-contrib-uglify 插件，专门用于JavaScript文件的压缩、混淆和优化
    uglify: {
      // 全局选项
      options: {
        // 在压缩后的文件开头添加的注释信息，添加版权信息、版本号等，便于识别和维护
        // 使用模板语法 <%= pkg.name %> 引用package.json中的项目名称
        // 使用 <%= grunt.template.today("yyyy-mm-dd") %> 获取当前日期
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("yyyy-mm-dd") %> */\n',
        // 启用变量名混淆
        // 将长变量名（如 userAuthenticationService ）替换为短名称（如 a 、 b ）
        // 减小文件大小，同时提高代码安全性（增加代码阅读难度）
        mangle: true,
        // 压缩选项
        compress: {
          // 删除所有 console.log 等控制台语句
          // 移除开发时的调试代码，减少生产环境文件大小，避免不必要的日志输出
          drop_console: true
        }
      },
      // 目标配置
      build: {
        // 要压缩的输入文件路径
        // 输入的是之前 concat 任务合并后的文件 dist/js/app.js
        src: 'dist/js/app.js',
        // 压缩后的输出文件路径
        // 输出到 dist/js/app.min.js ，带 .min 后缀标识为压缩版本
        dest: 'dist/js/app.min.js'
      }
    },
    
    // HTML压缩配置
    // 任务名称 ： htmlmin - 对应 grunt-contrib-htmlmin 插件，专门用于HTML文件的压缩和优化
    htmlmin: {
      // 开发环境
      // 所有压缩选项都设置为 false ，几乎不对HTML进行修改
      // 主要作用是将源HTML文件从 src/html 目录复制到 dist 目录
      // 保持HTML的原始格式和注释，便于开发和调试
      development: {
        options: {
          removeComments: false,
          collapseWhitespace: false,
          removeEmptyAttributes: false
        },
        files: {
          // 直接处理原始HTML文件： 'dist/index.html': 'src/html/index.html'
          'dist/index.html': 'src/html/index.html'
        }
      },
      // 生产环境
      production: {
        options: {
          // 删除所有HTML注释，如 <!-- 这是注释 -->
          removeComments: true,
          // 压缩所有空白字符（空格、换行、制表符等），只保留必要的空白
          collapseWhitespace: true,
          // 删除空属性，如 <div class=""></div> 变为 <div></div>
          removeEmptyAttributes: true,
          // 压缩HTML文件中的内联CSS代码（如 <style> 标签内的代码）
          minifyCSS: true,
          // 压缩HTML文件中的内联JavaScript代码（如 <script> 标签内的代码）
          minifyJS: true
        },
        files: {
          // 处理经过 processhtml 任务处理后的HTML文件： 'dist/index.min.html': 'dist/index.processed.html'
          // 这是因为生产环境需要先替换资源路径（通过processhtml），再进行压缩
          'dist/index.min.html': 'dist/index.processed.html'
        }
      }
    },
    
    // 资源路径替换配置
    // 任务名称 ： processhtml - 对应 grunt-processhtml 插件，专门用于在构建过程中处理HTML文件，主要功能是根据构建环境替换资源引用路径
    processhtml: {
      // 只配置了 production 环境，没有开发环境配置
      // 说明该任务主要用于生产构建，开发时不进行路径替换
      production: {
        options: {
          // 启用HTML处理功能
          // 该插件通过识别HTML文件中的特定注释标记（build comments）来实现资源路径替换
          process: true
        },
        files: {
          // src/html/index.html ：输入的源HTML文件
          // dist/index.processed.html ：处理后的临时HTML文件，注意：这是一个临时文件，后续会被 htmlmin 任务进一步压缩
          'dist/index.processed.html': 'src/html/index.html'
        }
      }
    },
    
    // 清理配置
    // 任务名称 ： clean - 对应 grunt-contrib-clean 插件，专门用于删除指定的文件或目录
    clean: {
      // 只配置了 production 环境，说明该任务主要用于生产构建后的清理
      // 开发环境通常不需要清理，以便保留中间文件用于调试
      production: {
        // src 数组指定了要清理的文件路径
        // dist/js/app.js ： concat 任务合并生成的JavaScript文件（非压缩版本）
        // dist/index.processed.html ： processhtml 任务生成的临时HTML文件（未压缩）
        src: ['dist/js/app.js', 'dist/index.processed.html']
      }
    },
    
    // 监控配置
    // 任务名称 ： watch - 对应 grunt-contrib-watch 插件，专门用于监控文件系统的变化并触发指定任务
    // 通过 grunt watch 或 grunt dev 命令启动监控服务
    watch: {
      // less子任务
      less: {
        // 使用glob模式监控 src/less 目录及其子目录下的所有 .less 文件
        files: ['src/less/**/*.less'],
        // 当任何LESS文件变化时，自动执行 less:development 任务（编译LESS为CSS）
        tasks: ['less:development']
      },
      // js子任务
      js: {
        // 监控 src/js 目录及其子目录下的所有 .js 文件
        files: ['src/js/**/*.js'],
        // 当任何JS文件变化时，自动执行 concat （合并JS）和 uglify （压缩JS）任务
        tasks: ['concat', 'uglify']
      },
      // html子任务
      html: {
        // 监控 src/html 目录及其子目录下的所有 .html 文件
        files: ['src/html/**/*.html'],
        // 当任何HTML文件变化时，自动执行 htmlmin:development 任务（处理HTML）
        tasks: ['htmlmin:development']
      }
    }
  });
  
  // 插件是怎么和任务对应的呢？
  // Grunt采用 插件名 → 任务名 → 任务配置 的三层对应结构（任务名与插件提供的任务名一致）：
  // grunt-contrib-less → less → less: {
  //   development: { ... },
  //   production: { ... }
  // }

  // 加载插件
  // Grunt提供的用于加载npm安装的Grunt插件的核心方法
  // 插件必须在 package.json 的 devDependencies 中声明并安装
  // 提供 less 任务，用于编译LESS为CSS
  grunt.loadNpmTasks('grunt-contrib-less');
  // 提供 concat 任务，用于合并文件
  grunt.loadNpmTasks('grunt-contrib-concat');
  // 提供 uglify 任务，用于压缩混淆JavaScript
  grunt.loadNpmTasks('grunt-contrib-uglify');
  // 提供 htmlmin 任务，用于压缩HTML
  grunt.loadNpmTasks('grunt-contrib-htmlmin');
  // 提供 watch 任务，用于监控文件变化自动构建
  grunt.loadNpmTasks('grunt-contrib-watch');
  // 提供 processhtml 任务，用于处理HTML资源路径
  grunt.loadNpmTasks('grunt-processhtml');
  // 提供 clean 任务，用于清理文件
  grunt.loadNpmTasks('grunt-contrib-clean');
  
  // Grunt提供的用于注册自定义任务的核心方法
  // grunt.registerTask('任务名', ['任务序列'])，将多个任务组合成一个命名的任务序列，方便用户调用

  // default任务 （默认任务）
  // 执行方式：直接运行grunt或npm run build
  // less:development - 编译LESS为未压缩的CSS（开发版本）
  // concat - 合并多个JavaScript文件
  // uglify - 压缩混淆JavaScript
  // htmlmin:development - 处理HTML文件（开发版本，几乎不压缩）
  grunt.registerTask('default', ['less:development', 'concat', 'uglify', 'htmlmin:development']);
  // production任务（生产构建任务）
  // 执行方式 ：运行 grunt production 或 npm run build:prod
  // less:production - 编译LESS为压缩的CSS（生产版本）
  // concat - 合并多个JavaScript文件
  // uglify - 压缩混淆JavaScript
  // processhtml:production - 替换HTML中的资源引用路径
  // htmlmin:production - 压缩HTML文件（生产版本）
  // clean:production - 清理构建过程中的中间文件
  grunt.registerTask('production', ['less:production', 'concat', 'uglify', 'processhtml:production', 'htmlmin:production', 'clean:production']);
  // dev任务 （开发监控任务）
  // 执行方式 ：运行 grunt dev 或 npm run dev
  // default - 先执行默认构建任务
  // watch - 然后启动文件监控，自动执行相关任务
  grunt.registerTask('dev', ['default', 'watch']);
};
