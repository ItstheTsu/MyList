<template>
  <div>
    <h2>Despesas do Usuário</h2>
    <ul>
      <li v-for="expense in expenses" :key="expense.id">
        {{ expense.description }} - Tipo da despesa: {{ expense.type }} - R$
        {{ expense.amount }} - Pago: {{ expense.paidText }} - Despesa registrada
        para: {{ expense.datate }}
        <button @click="deleteExpense(expense.id)">Deletar Despesa</button>
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

        this.expenses = response.data.map((exp) => {
          if (exp.date) {
            const [year, month, day] = exp.date.split("-");
            return {
              ...exp,
              datate: `${day}/${month}/${year}`,
              paidText: exp.paid ? "Pago" : "Não pago",
            };
          } else {
            return {
              ...exp,
              datate: "Sem data",
              paidText: exp.paid ? "Pago" : "Não pago",
            };
          }
        });

        console.log("Despesas:", this.expenses);
      } catch (error) {
        console.error("Erro ao carregar despesas:", error);
        if (error.response) {
          console.error("Status:", error.response.status);
          console.error("Data:", error.response.data);
        }
      }
    },

    async deleteExpense(expenseId) {
      try {
        await api.delete(`/expenses/${expenseId}`);
        this.expenses = this.expenses.filter((exp) => exp.id !== expenseId); // Remove da lista
        console.log(`Despesa ${expenseId} deletada`);
      } catch (error) {
        console.error(`Erro ao deletar despesa ${expenseId}:`, error);
      }
    },
  },
};
</script>
