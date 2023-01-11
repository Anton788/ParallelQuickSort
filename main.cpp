#include <iostream>
#include <vector>
#include <queue>

#include "parlay/parallel.h"
#include "parlay/primitives.h"
#include "parlay/sequence.h"
#include "Graph.h"

using namespace std;

vector<int> sequential_bfs(Graph &graph) {
    int count = graph.count();
    vector<int> dists(count, -1);
    vector<bool> visited(count, false);
    queue<pair<int, int>> node_queue{};
    pair<int, int> start;
    start.first = 0;
    start.second = 0;
    node_queue.push(start);
    while (!node_queue.empty()) {
        auto next_node = node_queue.front();
        node_queue.pop();
        if (dists[next_node.second] > -1){
            continue;
        }
        dists[next_node.second] = next_node.first;
        auto neighbours = graph.list_neighbours(next_node.second);
        for (int node: neighbours) {
            if (visited[node]){
                continue;
            } else {
                visited[node] = true;
                pair<int, int> pair1;
                pair1.first = next_node.first + 1;
                pair1.second = node;
                node_queue.push(pair1);
            }
        }
    }
    return dists;
}

parlay::sequence<int> parallel_bfs(Graph &graph) {
    int count = graph.count();
    auto visited = new std::atomic<bool>[count];
    parlay::sequence<int> dists(count, -1);
    for(int i = 0; i < count; i++) {
        visited[i] = false;
    }
    parlay::sequence<int> frontier(1, 0);
    for (int distance = 0; !frontier.empty(); distance++) {
        parlay::for_each(
                frontier,
                [&](int node) { dists[node] = distance; }
        );
        auto neighbours = parlay::flatten(parlay::map(
                frontier,
                [&](int node) {
                    return graph.list_neighbours(node);
                }
        ));

        frontier = parlay::filter(
                neighbours,
                [&] (int node) {
                    bool expected = false;
                    return visited[node].compare_exchange_strong(expected, true);
                }
        );
    }
    delete []visited;
    return dists;
}





int main() {
    Graph graph(500);

    auto start = chrono::high_resolution_clock::now();
    vector<int> distances_seq = sequential_bfs(graph);
    auto end = chrono::high_resolution_clock::now();
    cout << "Sequential: " << chrono::duration_cast<chrono::milliseconds>(end - start).count() << endl;
    for (int i = 0; i < distances_seq.size(); ++i){
        int value = distances_seq[i];
        int x = i % 500;
        int y = i / 500 % 500;
        int z = i / 500 / 500;
        if (value != x + y + z){
            cout << "Fail result";
            break;
        }
    }

    start = chrono::high_resolution_clock::now();
    parlay::sequence<int> distances_par = parallel_bfs(graph);
    end = chrono::high_resolution_clock::now();
    cout << "Parallel: " << chrono::duration_cast<chrono::milliseconds>(end - start).count() << endl;
    for (int i = 0; i < distances_par.size(); ++i){
        int value = distances_par[i];
        int x = i % 500;
        int y = i / 500 % 500;
        int z = i / 500 / 500;
        if (value != x + y + z){
            cout << "Fail result";
            break;
        }
    }
}
