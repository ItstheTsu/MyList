<template>
  <div class="dashboard-container">
    <div class="container-page" v-if="user.id">
      <div class="dashboard-header">
        <div class="dashboard-header-top">
          <h1>Dashboard Financeiro - {{ user.name }}</h1>
          <div class="dashboard-header-buttons">
            <button @click="config"><i class="fa-solid fa-gear"></i></button>
            <button @click="logout"><i class="fa-solid fa-arrow-right-from-bracket"></i></button>
          </div>
        </div>
        <div class="dashboard-header-bottom">
          Salário Mensal: {{ user.salary }} | Limite Mensal:
          {{ user.limitValue }} | Moeda Atual: {{ user.currency }}
        </div>
      </div>

      <h1>Despesas do usuário</h1>

      <ExpenseForm :userId="user.id" @expense-added="refreshExpenses" />
      <ExpenseList :userId="user.id" :refresh="refreshFlag" />
    </div>

    <div v-else>
      <p>Carregando usuário...</p>
    </div>
  </div>
</template>

<script>
import ExpenseList from "../components/ExpenseList.vue";
import ExpenseForm from "../components/ExpenseForm.vue";
import { useRouter } from "vue-router";
import api from "../services/api";
import "../styles/Dashboard/Dashboard.css";

export default {
  components: { ExpenseList, ExpenseForm },
  data() {
    return {
      user: {
        id: null,
        name: "",
        salary: null,
        limitValue: null,
        currency: null,
      },
      refreshFlag: false,
      router: null,
    };
  },
  async mounted() {
    this.router = useRouter();

    try {
      const userStorage = JSON.parse(localStorage.getItem("user"));

      if (!userStorage || !userStorage.id) {
        console.warn("Nenhum usuário logado");
        this.router.push("/login");
        return;
      }

      this.user = userStorage;

      const response = await api.get(`/users/${this.user.id}`);
      this.user = response.data;

      localStorage.setItem("user", JSON.stringify(this.user));
    } catch (error) {
      console.error("Erro ao carregar usuário logado:", error);
      this.router.push("/login");
    }
  },
  methods: {
    refreshExpenses() {
      this.refreshFlag = !this.refreshFlag;
    },
    logout() {
      this.user = { id: null, name: "" };
      localStorage.removeItem("user");
      this.router.push("/login");
    },
    config() {
      this.router.push("/config");
    },
  },
};
</script>
