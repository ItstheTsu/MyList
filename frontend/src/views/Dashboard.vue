<template>
  <div v-if="user.id">
    <div
      style="display: flex; justify-content: space-between; align-items: center"
    >
      <h1>Dashboard Financeiro - {{ user.name }}</h1>
      <button @click="logout">Logout</button>
    </div>
    <ExpenseForm :userId="user.id" @expense-added="refreshExpenses" />
    <ExpenseList :userId="user.id" :refresh="refreshFlag" />
  </div>
  <div v-else>
    <p>Carregando usuário...</p>
  </div>
</template>

<script>
import ExpenseList from "../components/ExpenseList.vue";
import ExpenseForm from "../components/ExpenseForm.vue";
import { useRouter } from "vue-router";

export default {
  components: {
    ExpenseList,
    ExpenseForm,
  },
  data() {
    return {
      user: { id: null, name: "" },
      refreshFlag: false,
    };
  },
  async mounted() {
    try {
      const userStorage = localStorage.getItem("user");
      if (userStorage) {
        this.user = JSON.parse(userStorage);
      } else {
        console.warn("Nenhum usuário logado");
        this.router.push("/login");
      }
    } catch (error) {
      console.error("Erro ao carregar usuário logado:", error);
    }
  },
  setup() {
    const router = useRouter();
    return { router };
  },
  methods: {
    refreshExpenses() {
      this.refreshFlag = !this.refreshFlag;
    },
    logout() {
      this.user = { id: null, name: "" };
      localStorage.removeItem("user"); // limpa o LocalStorage
      this.router.push("/login");
    },
  },
};
</script>