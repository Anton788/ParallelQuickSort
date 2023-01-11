#include <vector>


struct Graph {
    int size = 0;
public:
    Graph(int size) {
        this->size = size;
    }
    int order(int x, int y, int z) const {
        return x + y * size + z * size * size;
    }
    int count() const {
        return size * size * size;
    }
    std::vector<int> list_neighbours(int node) {
        int x = node % size;
        int y = node / size % size;
        int z = node / size / size;
        std::vector<int> neighbours(0);
        if (x < size - 1) {
            neighbours.push_back(order(x + 1, y, z));
        }
        if (y < size - 1) {
            neighbours.push_back(order(x, y + 1, z));
        }
        if (z < size - 1) {
            neighbours.push_back(order(x, y, z + 1));
        }
        return neighbours;
    }
};
