<template>
  <div class="operator-search">
    <input
  type="text"
  v-model="searchQuery"
  @input="onSearch"
  id="operator-search-input" 
  name="operator-search"
  placeholder="Search for operators..."
  class="search-input"
    />
    <button @click="onSearch" class="search-button">Search</button>
  </div>
</template>

<script>
export default {
  name: 'OperatorSearch',
  data() {
    return {
      searchQuery: '',
      debounceTimer: null
    };
  },
  methods: {
    onSearch() {
      clearTimeout(this.debounceTimer);
      this.debounceTimer = setTimeout(() => {
        if (this.searchQuery.trim()) {
          this.$emit('search', this.searchQuery.trim());
        }
      }, 300);
    }
  }
};
</script>

<style scoped>
.operator-search {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  max-width: 600px;
  margin: 20px auto;
}

.search-input {
  flex: 1;
  padding: 12px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 8px 0 0 8px;
  outline: none;
}

.search-button {
  padding: 12px 20px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 0 8px 8px 0;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.3s;
}

.search-button:hover {
  background-color: #369f6b;
}

@media (max-width: 600px) {
  .operator-search {
    flex-direction: column;
    align-items: center;
  }

  .search-input, .search-button {
    width: 100%;
    border-radius: 8px;
    margin-bottom: 10px;
  }
}
</style>
