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
  %contador_items = alloca i32
  %capacidad_maxima = alloca i32
  %items_rechazados = alloca i32
  %prioridad = alloca i32
  %x = alloca i32
  %peso_total = alloca double
  %peso_limite = alloca double
  %promedio_peso = alloca double
  %volumen_paquete = alloca double
  %peso_float = alloca double
  %sistema_activo = alloca i1
  %error_sensor = alloca i1
  %es_urgente = alloca i1
  %registros_pesado = alloca i32
  store i32 0, i32* %contador_items
  store i32 10, i32* %capacidad_maxima
  store double 0.0, double* %peso_total
  store double 500.50, double* %peso_limite
  store i1 true, i1* %sistema_activo
  store i1 false, i1* %error_sensor
  store i32 2, i32* %x
  %1 = alloca [3 x double]
  %2 = getelementptr [3 x double], [3 x double]* %1, i32 0, i32 0
  store double 1.0, double* %2
  %3 = getelementptr [3 x double], [3 x double]* %1, i32 0, i32 1
  store double 2.0, double* %3
  %4 = getelementptr [3 x double], [3 x double]* %1, i32 0, i32 2
  store double 0., double* %4
  %5 = getelementptr [3 x double], [3 x double]* %1, i32 0, i32 0
  store ptr %5, ptr %registros_pesado
  %6 = getelementptr double, double* %registros_pesado, i32 1
  %7 = load double, double* %6
  store double %7, double* %peso_total
  %8 = load i32, i32* %x
  %9 = add i32 0, %8
  %10 = getelementptr double, double* %registros_pesado, i32 %9
  %11 = load double, double* %10
  store double %11, double* %peso_total
  %12 = load i32, i32* %x
  %13 = add i32 0, %12
  %14 = getelementptr double, double* %registros_pesado, i32 %13
  %15 = load double, double* %14
  store double %15, double* %peso_total
  br label %label_1

label_1:
  %16 = load i32, i32* %contador_items
  %17 = load i32, i32* %capacidad_maxima
  %18 = icmp slt i32 %16, %17
  %19 = load i1, i1* %sistema_activo
  %20 = and i1 %18, %19
  br i1 %20, label %label_2, label %label_3

label_2:
  %21 = load i32, i32* %contador_items
  %22 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.int, i32 0, i32 0), i32 %21)
  store double 15.0, double* %volumen_paquete
  %23 = load double, double* %peso_total
  %24 = fmul double 45.5, 2.0
  %25 = fadd double %23, %24
  %26 = fdiv double %25, 1.1
  store double %26, double* %peso_total
  %27 = load i32, i32* %contador_items
  %28 = icmp ne i32 %27, 5
  br i1 %28, label %label_4, label %label_5

label_4:
  br label %label_5

label_5:
  %30 = load i32, i32* %contador_items
  %31 = icmp sle i32 %30, 2
  %32 = load double, double* %peso_total
  %33 = load double, double* %peso_limite
  %34 = fcmp oge double %32, %33
  %35 = or i1 %31, %34
  br i1 %35, label %label_6, label %label_7

label_6:
  br label %label_7

label_7:
  %37 = load i32, i32* %contador_items
  %38 = icmp eq i32 %37, 8
  br i1 %38, label %label_8, label %label_9

label_8:
  br label %label_3

label_9:
  %40 = load i1, i1* %error_sensor
  br i1 %40, label %label_10, label %label_11

label_10:
  br label %label_1

label_11:
  %41 = load i32, i32* %contador_items
  %42 = add i32 %41, 1
  store i32 %42, i32* %contador_items
  br label %label_1

label_3:
  store i32 0, i32* %contador_items
  store i32 0, i32* %items_rechazados
  br label %label_12

label_12:
  %43 = load i32, i32* %contador_items
  %44 = icmp slt i32 %43, 5
  br i1 %44, label %label_13, label %label_14

label_13:
  %45 = load i32, i32* %contador_items
  %46 = add i32 %45, 1
  store i32 %46, i32* %contador_items
  %47 = load i32, i32* %contador_items
  %48 = icmp eq i32 %47, 3
  br i1 %48, label %label_15, label %label_16

label_15:
  br label %label_12

label_16:
  %49 = load i32, i32* %items_rechazados
  %50 = add i32 %49, 1
  store i32 %50, i32* %items_rechazados
  br label %label_12

label_14:
  %51 = load i32, i32* %items_rechazados
  %52 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.int, i32 0, i32 0), i32 %51)
  store i32 0, i32* %prioridad
  br label %label_17

label_17:
  %53 = load i32, i32* %prioridad
  %54 = icmp slt i32 %53, 10
  br i1 %54, label %label_18, label %label_19

label_18:
  %55 = load i32, i32* %prioridad
  %56 = add i32 %55, 1
  store i32 %56, i32* %prioridad
  %57 = load i32, i32* %prioridad
  %58 = icmp eq i32 %57, 4
  br i1 %58, label %label_20, label %label_21

label_20:
  br label %label_19

label_21:
  br label %label_17

label_19:
  %59 = load i32, i32* %prioridad
  %60 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.int, i32 0, i32 0), i32 %59)
  %61 = load double, double* %peso_total
  %62 = fcmp ogt double %61, 400.0
  %63 = load i1, i1* %error_sensor
  %64 = xor i1 %63, true
  %65 = and i1 %62, %64
  br i1 %65, label %label_22, label %label_25

label_22:
  br label %label_23

label_25:
  %67 = load double, double* %peso_total
  %68 = fcmp ogt double %67, 200.0
  br i1 %68, label %label_26, label %label_27

label_26:
  br label %label_27

label_27:
  br label %label_23

label_24:
  br label %label_23

label_23:
  %71 = alloca [3 x double]
  %72 = getelementptr [3 x double], [3 x double]* %71, i32 0, i32 0
  store double 10.0, double* %72
  %73 = getelementptr [3 x double], [3 x double]* %71, i32 0, i32 1
  store double 20.5, double* %73
  %74 = getelementptr [3 x double], [3 x double]* %71, i32 0, i32 2
  store double 30.0, double* %74
  %75 = getelementptr [3 x double], [3 x double]* %71, i32 0, i32 0
  %76 = call double @suma_cumulativa(i32 5, ptr %75)
  store double %76, double* %promedio_peso
  %77 = load double, double* %promedio_peso
  %78 = call i32 (i8*, ...) @printf(i8* getelementptr inbounds ([4 x i8], [4 x i8]* @.str.float, i32 0, i32 0), double %77)
  %79 = load i1, i1* %sistema_activo
  %80 = xor i1 %79, true
  %81 = load i32, i32* %contador_items
  %82 = icmp eq i32 %81, 0
  %83 = or i1 %80, %82
  br i1 %83, label %label_28, label %label_30

label_28:
  br label %label_29

label_30:
  br label %label_29

label_29:
  ret i32 0
}
