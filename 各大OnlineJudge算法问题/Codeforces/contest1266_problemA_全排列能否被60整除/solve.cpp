#include <iostream>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <algorithm>
#include <cmath>
#include <climits>
using namespace std;
 
bool solve(char * s) {
    int lens = (int) strlen(s);
    int zero_i = -1;
    for (int i = 0; i < lens; i ++) {
        if (s[i] == '0') {
            zero_i = i;
            break;
        }
    }
    if (zero_i == -1) return false;
    int m = 0;
    for (int i = 0; i < lens; i ++) {
        if (i == zero_i) continue;
        int t = s[i] - '0';
        m = (m + (t * 4)) % 6;
    }
    for (int i = 0; i < lens; i ++) {
        if (i == zero_i) continue;
        int t = s[i] - '0';
        int x = (-3 * t) % 6;
        if ((m + x) % 6 == 0) return true;
    }
    return false;
}
 
int main() {
    int n;
    cin >> n;
    for (int ii = 0; ii < n; ii ++) {
        char s[105];
        cin >> s;
        printf("%s\n", solve(s) ? "red" : "cyan");
    }
    return 0;
}
