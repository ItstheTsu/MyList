<template>
  <div>
    <p>Total não pago: R$ {{ totalNaoPago.toFixed(2) }}</p>
    <p>Total pago: R$ {{ totalPago.toFixed(2) }}</p>

    <ul>
      <li
        v-for="expense in expenses"
        :key="expense.id"
      >
        {{ expense.description }} - Tipo: {{ expense.type }} - R$
        {{ expense.amount }} - Para pagamento em: {{ expense.datate }}

        <span v-if="expense.endDate">
          - Vence da conta em: {{ expense.dataFinal }}
        </span>

        <span>
          <label>Alterar status da conta:</label>
          <button
            :class="expense.paid ? 'paid-btn' : 'unpaid-btn'"
            @click="togglePaid(expense)"
          >
            {{ expense.paid ? "Não pago" : "Pago" }}
          </button>
        </span>

        <button @click="deleteExpense(expense.id)">Deletar</button>
      </li>
    </ul>

    <button @click="marcarTodosComoPagos">Marcar todos como pagos</button>
    <button @click="marcarTodosComoNaoPagos">
      Marcar todos como não pagos
    </button>
  </div>
</template>

<script>
import api from "../services/api";

export default {
  name: "ExpenseList",
  props: {
    userId: { type: Number, required: true },
    refresh: { type: Boolean, default: false },
  },
  data() {
    return { expenses: [] };
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
          const dataFinal = exp.endDate
            ? exp.endDate.split("-").reverse().join("/")
            : "—";
          return { ...exp, datate, dataFinal };
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
      const newStatus = !expense.paid;
      try {
        await api.put(`/expenses/paid/${expense.id}`, { paid: newStatus });
        expense.paid = newStatus;
      } catch (error) {
        console.error(`Erro ao atualizar despesa ${expense.id}:`, error);
      }
    },

    async marcarTodosComoPagos() {
      const promises = this.expenses
        .filter((exp) => !exp.paid)
        .map((exp) => {
          exp.paid = true;
          return api.put(`/expenses/paid/${exp.id}`, { paid: true });
        });
      try {
        await Promise.all(promises);
      } catch {
        this.loadExpenses();
      }
    },

    async marcarTodosComoNaoPagos() {
      const promises = this.expenses
        .filter((exp) => exp.paid)
        .map((exp) => {
          exp.paid = false;
          return api.put(`/expenses/paid/${exp.id}`, { paid: false });
        });
      try {
        await Promise.all(promises);
      } catch {
        this.loadExpenses();
      }
    },
  },
};
</script>
