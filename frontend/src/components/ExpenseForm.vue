<template>
  <div class="expense-form-container">
    <button @click="openModal" class="add-btn">+ Adicionar Despesa</button>

    <transition name="fade">
      <div class="overlay" v-if="isModalOpen" @click="closeModal"></div>
    </transition>

    <transition name="modal">
      <div class="modal" v-if="isModalOpen">
        <h2>Adicionar Despesa</h2>

        <form @submit.prevent="submitExpense">
          <label>Descrição:</label>
          <input v-model="form.description" placeholder="Descrição" required />

          <label>Valor:</label>
          <input v-model.number="form.amount" type="number" required />

          <label>Tipo:</label>
          <select v-model="form.type">
            <option value="FIXO">Fixo</option>
            <option value="VARIAVEL">Variável</option>
          </select>

          <label>Data de pagamento:</label>
          <input type="date" v-model="form.date" required />

          <div class="actions">
            <button type="button" @click="closeModal">Cancelar</button>
            <button type="submit">Salvar</button>
          </div>
        </form>
      </div>
    </transition>
  </div>
</template>

<script>
import api from "../services/api";
import "../styles/ExpenseForm/ExpenseForm.scss";

export default {
  name: "ExpenseForm",
  props: ["userId"],
  data() {
    return {
      isModalOpen: false,
      form: {
        description: "",
        amount: 0,
        type: "FIXO",
        date: "",
      },
    };
  },
  methods: {
    openModal() {
      this.isModalOpen = true;
    },
    closeModal() {
      this.isModalOpen = false;
    },
    async submitExpense() {
      const today = new Date();
      const paymentDate = new Date(this.form.date);
      let delayed = false;

      if (paymentDate < today) {
        const paid = confirm("Essa conta já foi paga?");
        if (!paid) delayed = true;
      }

      const expense = {
        ...this.form,
        date: paymentDate.toISOString().split("T")[0],
        user: { id: this.userId },
        delayed,
      };

      try {
        await api.post("/expenses", expense);
        this.closeModal();
        this.$emit("expense-added");
      } catch (err) {
        console.error("Erro ao salvar despesa:", err);
      }
    },
  },
};
</script>