import gulp from 'gulp';
import htmlmin from 'gulp-htmlmin';
import cleanCSS from 'gulp-clean-css';
import terser from 'gulp-terser';
import imagemin from 'gulp-imagemin';
import rename from 'gulp-rename';
import sourcemaps from 'gulp-sourcemaps';

// HTML 构建任务
gulp.task('html', function() {
  return gulp.src('src/html/*.html')
    .pipe(htmlmin({
      collapseWhitespace: true,
      removeComments: true,
      minifyCSS: true,
      minifyJS: true
    }))
    .pipe(gulp.dest('dist'));
});

// CSS 构建任务
gulp.task('css', function() {
  return gulp.src('src/css/*.css')
    .pipe(sourcemaps.init())
    .pipe(cleanCSS({
      compatibility: 'ie8'
    }))
    .pipe(rename({ suffix: '.min' }))
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest('dist/css'));
});

// JS 构建任务
gulp.task('js', function() {
  return gulp.src('src/js/*.js')
    .pipe(sourcemaps.init())
    .pipe(terser({
      compress: {
        drop_console: true
      }
    }))
    .pipe(rename({ suffix: '.min' }))
    .pipe(sourcemaps.write('.'))
    .pipe(gulp.dest('dist/js'));
});

// 图片构建任务
gulp.task('images', function() {
  return gulp.src('src/images/*')
    .pipe(imagemin({
      optimizationLevel: 5,
      progressive: true,
      interlaced: true
    }))
    .pipe(gulp.dest('dist/images'));
});

// 监视任务
gulp.task('watch', function() {
  gulp.watch('src/html/*.html', gulp.series('html'));
  gulp.watch('src/css/*.css', gulp.series('css'));
  gulp.watch('src/js/*.js', gulp.series('js'));
  gulp.watch('src/images/*', gulp.series('images'));
});

// 构建任务
gulp.task('build', gulp.parallel('html', 'css', 'js', 'images'));

// 默认任务
gulp.task('default', gulp.series('build', 'watch'));