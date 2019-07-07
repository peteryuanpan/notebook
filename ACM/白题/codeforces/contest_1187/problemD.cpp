#include <cstdio>
#include <cstdlib>
#include <vector>
#include <algorithm>
#include <cstring>

#define lowbit(x) x&-x
using namespace std;
const int N=3e5+50;
int t,n,a[N],b[N],p[N],f[N];
std::vector<int>v[N];

void change(int x,int k){
	for(;x<=n;x+=lowbit(x))f[x]=max(f[x],k);
}

int ask(int x){
	int ret=0;
	for(;x;x^=lowbit(x))ret=max(ret,f[x]);
	return ret;
}

int main(){
	scanf("%d",&t);
	while(t--){
		scanf("%d",&n);bool flag=0;
		for(int i=1;i<=n;i++)scanf("%d",&a[i]),v[a[i]].clear();
		for(int i=1;i<=n;i++)scanf("%d",&b[i]),v[b[i]].push_back(i);
		for(int i=n;i;i--){
			if(!v[a[i]].size())flag=1;
			else p[i]=v[a[i]][v[a[i]].size()-1],v[a[i]].pop_back();
		}
		for(int i=1;i<=n;i++)f[i]=0;
		for(int i=1;i<=n;i++){
			if(ask(a[i]-1)>p[i])flag=1;
			change(a[i],p[i]);
		}
		puts(flag?"NO":"YES");
	}
	return 0;
}
