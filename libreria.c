// libreria.c
double suma_cumulativa(int cantidad, double* arreglo) {
    double total = 0.0;
    for(int i = 0; i < cantidad; i++) {
        total += arreglo[i];
    }
    return total;
}