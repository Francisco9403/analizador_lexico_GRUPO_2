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
  %contador = alloca i32
  %sumatoria = alloca i32
  store i32 0, i32* %contador
  store i32 0, i32* %sumatoria
  br label %label_1

label_1:
  %1 = load i32, i32* %contador
  %2 = icmp slt i32 %1, 10
  br i1 %2, label %label_2, label %label_3

label_2:
  %3 = load i32, i32* %contador
  %4 = add i32 %3, 1
  store i32 %4, i32* %contador
  %5 = load i32, i32* %contador
  %6 = icmp eq i32 %5, 3
  br i1 %6, label %label_4, label %label_5

label_4:
  br label %label_1

label_5:
  %7 = load i32, i32* %contador
  %8 = icmp eq i32 %7, 7
  br i1 %8, label %label_6, label %label_7

label_6:
  br label %label_3

label_7:
  %9 = load i32, i32* %sumatoria
  %10 = load i32, i32* %contador
  %11 = add i32 %9, %10
  store i32 %11, i32* %sumatoria
  br label %label_1

label_3:
  %12 = load i32, i32* %sumatoria
  %13 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.int, i32 0, i32 0), i32 %12)
  ret i32 0
}
