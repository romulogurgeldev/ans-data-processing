<template>
  <div class="operator-list">
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
    <div v-else-if="loading" class="loading">Loading...</div>
    <div v-else-if="operators.length === 0" class="no-results">
      No operators found
    </div>
    <ul v-else class="operator-items">
      <li v-for="operator in operators" :key="operator.id" class="operator-item">
        <div class="operator-info">
          <h3>{{ operator.trade_name || operator.legal_name }}</h3>
          <p v-if="operator.legal_name && operator.legal_name !== operator.trade_name">
            Legal Name: {{ operator.legal_name }}
          </p>
          <p>Registry: {{ operator.registry }}</p>
          <p>CNPJ: {{ operator.cnpj }}</p>
          <p>Modality: {{ operator.modality }}</p>
          <p>Location: {{ operator.city }}/{{ operator.state }}</p>
        </div>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  props: {
    operators: {
      type: Array,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    error: {
      type: String,
      default: null
    }
  }
}
</script>

<style scoped>
.error-message {
  color: #dc3545;
  background: #f8d7da;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 20px;
  text-align: center;
}

.operator-list {
  margin-top: 20px;
}

.loading, .no-results {
  text-align: center;
  padding: 20px;
  color: #666;
}

.operator-items {
  list-style: none;
  padding: 0;
}

.operator-item {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 10px;
  transition: box-shadow 0.3s;
}

.operator-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.operator-info h3 {
  margin-top: 0;
  color: #2c3e50;
}

.operator-info p {
  margin: 5px 0;
  color: #666;
}
</style>