<template>
  <div>
    <h2>Despesas do Usuário</h2>
    <ul>
      <li v-for="expense in expenses" :key="expense.id">
        {{ expense.description }} - {{ expense.type }} - R$ {{ expense.amount }} - Pago: {{ expense.paidText }}
      </li>
    </ul>
  </div>
</template>

<script>
import api from "../services/axios";

export default {
  name: "ExpenseList",
  data() {
    return {
      expenses: [],
      userId: 1,
    };
  },
  mounted() {
    this.loadExpenses();
  },
  methods: {
    async loadExpenses() {
      try {
        const response = await api.get(`/expenses/user/${this.userId}`);
        // Mapear paid para texto
        this.expenses = response.data.map(exp => ({
          ...exp,
          paidText: exp.paid ? "Pago" : "Não pago"
        }));
        console.log("Despesas:", this.expenses);
      } catch (error) {
        console.error("Erro ao carregar despesas:", error);
        if (error.response) {
          console.error("Status:", error.response.status);
          console.error("Data:", error.response.data);
        }
      }
    }
  }
};
</script>