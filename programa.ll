; Compilador UNNOBA 2026
target datalayout = "e-m:w-p270:32:32-p271:32:32-p272:64:64-i64:64-f80:128-n8:16:32:64-S128"
target triple = "x86_64-pc-windows-msvc"

declare i32 @printf(i8*, ...)
declare i32 @scanf(i8*, ...)
declare double @suma_cumulativa(i32, ptr)

@.str.int = private constant [4 x i8] c"%d\0A\00"
@.str.float = private constant [4 x i8] c"%f\0A\00"

define i32 @main() {
entry:
  %valores = alloca i32
  %aux = alloca double
  %1 = alloca [3 x double]
  %2 = getelementptr [3 x double], [3 x double]* %1, i32 0, i32 0
  store double 10.0, double* %2
  %3 = getelementptr [3 x double], [3 x double]* %1, i32 0, i32 1
  store double 20.0, double* %3
  %4 = getelementptr [3 x double], [3 x double]* %1, i32 0, i32 2
  store double 30.0, double* %4
  %5 = getelementptr [3 x double], [3 x double]* %1, i32 0, i32 0
  store ptr %5, ptr %valores
  %6 = getelementptr double, double* %valores, i32 1
  %7 = load double, double* %6
  store double %7, double* %aux
  %8 = load double, double* %aux
  %9 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.float, i32 0, i32 0), double %8)
  %10 = getelementptr double, double* %valores, i32 1
  %11 = load double, double* %10
  store double %11, double* %aux
  %12 = load double, double* %aux
  %13 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.float, i32 0, i32 0), double %12)
  ret i32 0
}
