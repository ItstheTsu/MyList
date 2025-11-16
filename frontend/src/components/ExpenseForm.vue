<template>
  <div>
    <h2>Adicionar Despesa</h2>
    <form @submit.prevent="addExpense">
      <div>
        <label>Descrição:</label>
        <input v-model="expense.description" required />
      </div>
      <div>
        <label>Valor:</label>
        <input type="number" v-model="expense.amount" required />
      </div>
      <div>
        <label>Tipo:</label>
        <select v-model="expense.type" required>
          <option value="FIXO">Fixo</option>
          <option value="VARIAVEL">Variável</option>
        </select>
      </div>
      <div>
        <label>Data para pagamento:</label>
        <input type="date" v-model="expense.date" required />
      </div>
      <button type="submit">Adicionar</button>
    </form>
  </div>
</template>

<script>
import api from "../services/axios";

export default {
  name: "ExpenseForm",
  props: ["userId"],
  data() {
    return {
      expense: {
        description: "",
        amount: 0,
        type: "FIXO",
        date: "",
      },
    };
  },
  methods: {
    async addExpense() {
      try {
        const payload = { ...this.expense, user: { id: this.userId } };
        await api.post("/expenses", payload);
        //reseta o formulário
        this.expense.description = "";
        this.expense.amount = 0;
        this.expense.type = "FIXO";
        this.expense.date = "";
        this.$emit("expense-added");
      } catch (error) {
        console.error("Erro ao adicionar despesa:", error);
      }
    },
  },
};
</script>