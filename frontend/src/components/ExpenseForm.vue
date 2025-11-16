<template>
  <div>
    <button @click="openModal" class="add-btn">+ Adicionar Despesa</button>

    <dialog ref="modal" class="modal">
      <h2>Adicionar Despesa</h2>

      <form @submit.prevent="submitExpense">
        <label>Descrição:</label>
        <input v-model="form.description" required />

        <label>Valor:</label>
        <input v-model.number="form.amount" type="number" required />

        <label>Tipo:</label>
        <select v-model="form.type">
          <option value="FIXO">Fixo</option>
          <option value="VARIAVEL">Variável</option>
        </select>

        <label>Data de pagamento:</label>
        <input type="date" v-model="form.date" required />

        <label>
          <input type="checkbox" v-model="form.repeat" /> Repetir por meses
        </label>

        <div v-if="form.repeat && form.type === 'FIXO'">
          <label>Quantos meses?</label>
          <input type="number" v-model.number="form.months" min="1" />
        </div>

        <div v-if="form.repeat && form.type === 'VARIAVEL'">
          <button @click.prevent="openVariableEditor">
            Editar valores mensais
          </button>
        </div>

        <div class="actions">
          <button type="button" @click="closeModal">Cancelar</button>
          <button type="submit">Salvar</button>
        </div>
      </form>
    </dialog>

    <!-- modal de valores variáveis -->
    <dialog ref="variableModal" class="modal">
      <h2>Valores por mês</h2>

      <div v-for="(v, i) in variableValues" :key="i">
        <label>Mês {{ i + 1 }}</label>
        <input type="number" v-model.number="variableValues[i]" />
      </div>

      <button @click="closeVariableModal">Fechar</button>
    </dialog>
  </div>
</template>

<script>
import api from "../services/axios";

export default {
  name: "ExpenseForm",
  props: ["userId"],

  data() {
    return {
      form: {
        description: "",
        amount: 0,
        type: "FIXO",
        date: "",
        repeat: false,
        months: 1,
      },

      variableValues: [],
    };
  },

  methods: {
    openModal() {
      this.$refs.modal.showModal();
    },

    closeModal() {
      this.$refs.modal.close();
    },

    openVariableEditor() {
      this.variableValues = Array(this.form.months).fill(this.form.amount);
      this.$refs.variableModal.showModal();
    },

    closeVariableModal() {
      this.$refs.variableModal.close();
    },

    async submitExpense() {
      const today = new Date();
      const paymentDate = new Date(this.form.date);

      let delayed = false;
      let months = this.form.repeat ? this.form.months : 1;

      let firstPaymentDate = new Date(paymentDate);

      if (paymentDate < today) {
        const paid = confirm("Essa conta já foi paga?");
        if (!paid) {
          delayed = true;
          firstPaymentDate.setMonth(firstPaymentDate.getMonth() + 1);
        }
      }

      let finalEndDate = new Date(firstPaymentDate);
      finalEndDate.setMonth(finalEndDate.getMonth() + (months - 1));

      const finalEndStr = finalEndDate.toISOString().split("T")[0];

      const expense = {
        ...this.form,
        date: firstPaymentDate.toISOString().split("T")[0],
        endDate: finalEndStr,
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
