<template>
  <div v-if="user.id">
    <div style="display: flex; justify-content: space-between; align-items: center;">
      <h1>Dashboard Financeiro - {{ user.name }}</h1>
      <button @click="logout">Logout</button>
    </div>

    <!-- Form para adicionar despesa -->
    <ExpenseForm :userId="user.id" @expense-added="refreshExpenses" />

    <!-- Lista de despesas -->
    <ExpenseList :userId="user.id" :refresh="refreshFlag" />
  </div>
  <div v-else>
    <p>Carregando usuário...</p>
  </div>
</template>

<script>
import api from "../services/axios.js";
import ExpenseList from "../components/ExpenseList.vue";
import ExpenseForm from "../components/ExpenseForm.vue";
import { useRouter } from "vue-router"; // se estiver usando Vue Router

export default {
  components: {
    ExpenseList,
    ExpenseForm
  },
  data() {
    return {
      user: { id: null, name: '' },
      refreshFlag: false
    };
  },
  setup() {
    const router = useRouter();
    return { router };
  },
  async mounted() {
    try {
      const response = await api.get('/users/me');
      this.user = response.data;
    } catch (error) {
      console.error('Erro ao carregar usuário logado:', error);
    }
  },
  methods: {
    refreshExpenses() {
      this.refreshFlag = !this.refreshFlag;
    },
    logout() {
      // Limpar dados do usuário e/ou token
      this.user = { id: null, name: '' };
      localStorage.removeItem("token"); // se estiver usando token
      // Redirecionar para login
      this.router.push("/login");
    }
  }
};
</script>
