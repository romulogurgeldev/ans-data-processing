<template>
  <div class="operator-container">
    <div v-if="error" class="error-message">{{ error }}</div>
    <div v-else-if="loading" class="loading">Loading...</div>
    <div v-else-if="!operators || operators.length === 0" class="no-results">No operators found</div>
    
    <ul v-else class="operator-list">
      <li v-for="operator in operators" :key="operator.id" class="operator-item">
        <div class="operator-info">
          <h3>{{ operator.tradeName || operator.legalName }}</h3>
          <p v-if="operator.legalName && operator.legalName !== operator.tradeName">
            <strong>Legal Name:</strong> {{ operator.legalName }}
          </p>
          <p><strong>Registry:</strong> {{ operator.registry }}</p>
          <p><strong>CNPJ:</strong> {{ operator.cnpj }}</p>
          <p><strong>Modality:</strong> {{ operator.modality }}</p>
          <p><strong>Location:</strong> {{ operator.city }}/{{ operator.state }}</p>
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
      default: () => []
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
};
</script>

<style scoped>
.operator-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  max-width: 800px;
  margin: auto;
  text-align: center;
}

.error-message, .loading, .no-results {
  text-align: center;
  padding: 15px;
  font-size: 16px;
  width: 100%;
}

.error-message {
  color: #dc3545;
  background: #f8d7da;
  border-radius: 8px;
}

.operator-list {
  list-style: none;
  padding: 0;
  width: 100%;
  max-width: 600px;
}

.operator-item {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.operator-item:hover {
  transform: translateY(-3px);
}

.operator-info h3 {
  margin: 0 0 10px;
  color: #2c3e50;
}

.operator-info p {
  margin: 5px 0;
  color: #666;
}
</style>
