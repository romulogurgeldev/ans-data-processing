<template>
  <div class="operator-search">
    <input
      type="text"
      v-model="searchQuery"
      @input="onSearch"
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
    }
  },
  methods: {
    onSearch() {
      clearTimeout(this.debounceTimer)
      this.debounceTimer = setTimeout(() => {
        this.$emit('search', this.searchQuery)
      }, 300)
    }
  }
}
</script>

<style scoped>
.operator-search {
  display: flex;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ccc;
  border-radius: 4px 0 0 4px;
}

.search-button {
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
  font-size: 16px;
}

.search-button:hover {
  background-color: #369f6b;
}
</style>