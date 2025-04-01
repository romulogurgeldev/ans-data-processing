<template>
  <div class="home">
    <h1>ANS Operator Search</h1>
    <OperatorSearch @search="handleSearch" />
    <OperatorList 
      :operators="operators || []" 
      :loading="loading" 
      :error="error"
    />
  </div>
</template>

<script setup>
import OperatorSearch from '@/components/OperatorSearch.vue';
import OperatorList from '@/components/OperatorList.vue';
import { ref } from 'vue';
import api from '@/api';

const operators = ref([]);
const loading = ref(false);
const error = ref(null);

const handleSearch = async (query) => {
  if (query.length < 2) {
    operators.value = [];
    error.value = null;
    return;
  }

  loading.value = true;
  error.value = null;
  
  try {
    const response = await api.get('/operators/search', { 
      params: { 
        query: query,
        _t: Date.now()
      }
    });
    // Garante que operators seja sempre um array
    operators.value = Array.isArray(response.data) ? response.data : [];
  } catch (err) {
    error.value = err.response?.data?.message || 
                 err.message || 
                 'Erro desconhecido ao buscar operadoras';
    operators.value = []; // Reseta para array vazio
    
    console.error('Detalhes do erro:', {
      status: err.response?.status,
      data: err.response?.data,
      message: err.message
    });
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.home {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  text-align: center;
}

h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  h1 {
    font-size: 1.5rem;
  }
}
</style>
