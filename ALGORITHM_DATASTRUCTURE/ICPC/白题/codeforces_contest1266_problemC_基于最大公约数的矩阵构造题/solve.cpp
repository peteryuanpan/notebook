#include <iostream>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <algorithm>
#include <cmath>
#include <climits>
using namespace std;
 
const int M = 505;
int result[M][M];
 
void build(int r, int c) {
    int t = 1;
    for (int j = 1; j <= c; j ++) {
        t ++;
        result[1][j] = t;
    }
    for (int i = 2; i <= r; i ++) {
        t ++;
        for (int j = 1; j <= c; j ++) {
            result[i][j] = t * result[1][j];
        }
    }
}
 
int main() {
    int r, c;
    cin >> r >> c;
 
    if (r == 1 && c == 1) {
        printf("0\n");
        return 0;
    }
 
    if (r <= c) {
        build(r, c);
        for (int i = 1; i <= r; i ++) {
            for (int j = 1; j <= c; j ++) {
                printf("%d%c", result[i][j], j == c ? '\n' : ' ');
            }
        }
    }
    else {
        build(c, r);
        for (int j = 1; j <= r; j ++) {
            for (int i = 1; i <= c; i ++) {
                printf("%d%c", result[i][j], i == c ? '\n' : ' ');
            }
        }
    }
 
    return 0;
}
