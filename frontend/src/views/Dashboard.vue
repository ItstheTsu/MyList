<template>
  <div v-if="user.id">
    <div
      style="display: flex; justify-content: space-between; align-items: center"
    >
      <h1>Dashboard Financeiro - {{ user.name }}</h1>
      <button @click="config">Configurações</button>
      <button @click="logout">Logout</button>
    </div>
    <div>
      Salário Mensal: {{ user.salary }} | Limite Mensal: {{ user.limitValue }} |
      Moeda Atual: {{ user.currency }}
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
import axios from "axios";

export default {
  components: {
    ExpenseList,
    ExpenseForm,
  },
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
    };
  },
  async mounted() {
    try {
      const userStorage = JSON.parse(localStorage.getItem("user"));

      if (!userStorage || !userStorage.id) {
        console.warn("Nenhum usuário logado");
        this.router.push("/login");
        return;
      }

      this.user = userStorage;

      const response = await axios.get(
        `http://localhost:8080/api/users/${this.user.id}`
      );

      this.user = response.data;

      localStorage.setItem("user", JSON.stringify(response.data));
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
    config() {
      this.router.push("/config");
    },
  },
};
</script>
