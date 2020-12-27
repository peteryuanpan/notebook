class Solution {

    class Data {
        int apples;
        int from;
        int to;
        public Data(int apples, int from, int to) {
            this.apples = apples;
            this.from = from;
            this.to = to;
        }
    };
    PriorityQueue<Data> q = new PriorityQueue<Data>(new Comparator<Data>() {
        @Override
        public int compare(Data d1, Data d2) {
            return d1.to - d2.to;
        }
    });

    public int eatenApples(int[] apples, int[] days) {
        int ans = 0;
        int i = 1;
        while (true) {
            if (i-1 < apples.length) {
                Data d = new Data(apples[i-1], i, i + days[i-1] - 1);
                if (d.apples > 0)
                    q.add(d);
            }
            while (!q.isEmpty()) {
                Data d = q.poll();
                if (i >= d.from && i <= d.to) {
                    d.apples --;
                    ans ++;
                    if (d.apples > 0)
                        q.add(d);
                    break;
                }
            }
            i ++;
            if (i > apples.length && q.isEmpty())
                break;
        }

        return ans;
    }
}
