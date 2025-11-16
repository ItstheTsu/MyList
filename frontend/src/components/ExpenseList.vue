<template>
  <div>
    <h2>Despesas do Usuário</h2>

    <p>Total não pago: R$ {{ totalNaoPago.toFixed(2) }}</p>
    <p>Total pago: R$ {{ totalPago.toFixed(2) }}</p>

    <ul>
      <li
        v-for="expense in expenses"
        :key="expense.id"
        style="margin-bottom: 8px"
      >
        <input
          type="checkbox"
          v-model="expense.paid"
          @change="togglePaid(expense)"
        />
        {{ expense.description }} - Tipo: {{ expense.type }} - R$
        {{ expense.amount }} - Pago: {{ expense.paidText }} - Para pagamento em:
        {{ expense.datate }}
        <button @click="deleteExpense(expense.id)" style="margin-left: 10px">
          Deletar
        </button>
      </li>
    </ul>

    <button @click="marcarTodosComoPagos" style="margin-top: 10px">
      Marcar todos como pagos
    </button>
    <button
      @click="marcarTodosComoNaoPagos"
      style="margin-top: 10px; margin-left: 5px"
    >
      Marcar todos como não pagos
    </button>
  </div>
</template>

<script>
import api from "../services/axios";

export default {
  name: "ExpenseList",
  props: {
    userId: {
      type: Number,
      required: true,
    },
    refresh: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      expenses: [],
    };
  },
  computed: {
    totalNaoPago() {
      return this.expenses
        .filter((exp) => !exp.paid)
        .reduce((acc, exp) => acc + exp.amount, 0);
    },
    totalPago() {
      return this.expenses
        .filter((exp) => exp.paid)
        .reduce((acc, exp) => acc + exp.amount, 0);
    },
  },
  watch: {
    refresh() {
      this.loadExpenses();
    },
  },
  mounted() {
    this.loadExpenses();
  },
  methods: {
    async loadExpenses() {
      if (!this.userId) return;
      try {
        const response = await api.get(`/expenses/user/${this.userId}`);
        this.expenses = response.data.map((exp) => {
          const datate = exp.date
            ? exp.date.split("-").reverse().join("/")
            : "Sem data";
          return {
            ...exp,
            datate,
            paidText: exp.paid ? "Pago" : "Não pago",
          };
        });
      } catch (error) {
        console.error("Erro ao carregar despesas:", error);
      }
    },

    async deleteExpense(expenseId) {
      try {
        await api.delete(`/expenses/${expenseId}`);
        this.expenses = this.expenses.filter((exp) => exp.id !== expenseId);
      } catch (error) {
        console.error(`Erro ao deletar despesa ${expenseId}:`, error);
      }
    },

    async togglePaid(expense) {
      expense.paidText = expense.paid ? "Pago" : "Não pago";

      try {
        await api.put(`/expenses/paid/${expense.id}`, { paid: expense.paid });
      } catch (error) {
        console.error(`Erro ao atualizar despesa ${expense.id}:`, error);
        // Reverte caso dê erro
        expense.paid = !expense.paid;
        expense.paidText = expense.paid ? "Pago" : "Não pago";
      }
    },

    async marcarTodosComoPagos() {
      const promises = this.expenses
        .filter((exp) => !exp.paid)
        .map((exp) => {
          exp.paid = true;
          exp.paidText = "Pago";
          return api.put(`/expenses/paid/${exp.id}`, { paid: true });
        });

      try {
        await Promise.all(promises);
      } catch (error) {
        console.error("Erro ao marcar todos como pagos:", error);
        this.loadExpenses();
      }
    },

    async marcarTodosComoNaoPagos() {
      const promises = this.expenses
        .filter((exp) => exp.paid)
        .map((exp) => {
          exp.paid = false;
          exp.paidText = "Não pago";
          return api.put(`/expenses/paid/${exp.id}`, { paid: false });
        });

      try {
        await Promise.all(promises);
      } catch (error) {
        console.error("Erro ao marcar todos como não pagos:", error);
        this.loadExpenses();
      }
    },
  },
};
</script>
