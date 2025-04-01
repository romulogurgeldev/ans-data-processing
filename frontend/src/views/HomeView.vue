<template>
  <div class="home">
    <h1>ANS Operator Search</h1>
    <OperatorSearch @search="handleSearch" />
    <OperatorList :operators="operators" :loading="loading" :error="error" />
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
    operators.value = response.data;
  } catch (err) {
    error.value = err.response?.data?.message || 
                 err.message || 
                 'Erro desconhecido ao buscar operadoras';
    
    console.error('Erro completo:', {
      status: err.response?.status,
      data: err.response?.data,
      message: err.message,
      stack: err.stack
    });
  } finally {
    loading.value = false;
  }
};
</script>