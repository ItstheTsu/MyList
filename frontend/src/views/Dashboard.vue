<template>
  <div class="dashboard-container">
    <div class="container-page" v-if="user.id">
      <div class="dashboard-header">
        <div class="dashboard-header-top">
          <div class="dashboard-left">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <h1>MyList</h1>
          </div>
          <div class="dashboard-middle">
            <button @click="dashboard">
              <i class="fa-solid fa-chart-line"></i> Dashboard
            </button>
          </div>
          <div class="user-menu">
            <div class="trigger" @click="isOpen = !isOpen">
              {{ user.name }}
              <i class="fa-solid fa-chevron-down"></i>
              <div class="dropdown" v-if="isOpen">
                <button @click="config">
                  <i class="fa-solid fa-gear"></i> Configurações
                </button>
                <button @click="logout">
                  <i class="fa-solid fa-arrow-right-from-bracket"></i> Sair
                </button>
              </div>
            </div>
          </div>
        </div>
        <div class="dashboard-header-bottom">
          <div class="dash-bottom">
            Salário Mensal: {{ user.salary }} | Limite Mensal:
            {{ user.limitValue }} | Moeda Atual: {{ user.currency }}
          </div>
          <div class="dash-bottom">
            Total investido: {{ totalInvestido }} | Total gasto:
            {{ totalGasto }} | Restante mensal:
            {{ restanteMensal }}
          </div>
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
      isOpen: false,
      totalInvestido: 0,
      totalGasto: 0,
      restanteMensal: 0,
    };
  },
  async mounted() {
    this.router = useRouter();
    this.buscarTotais();

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
    async buscarTotais() {
      try {
        const response = await api.get(
          "http://localhost:8080/api/auth/finance/summary"
        );

        this.totalInvestido = response.data.totalInvestimentos;
        this.totalGasto = response.data.totalGasto;
        this.restanteMensal = this.user.limitValue - this.totalGasto;
      } catch (error) {
        console.error("Erro ao carregar totais:", error);
      }
    },

    refreshExpenses() {
      this.refreshFlag = !this.refreshFlag;
    },
    dashboard() {
      window.location.reload();
      this.router.push("/dashboard");
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
