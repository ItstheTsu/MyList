<template>
  <div class="config-container">
    <h2>Configurações</h2>
    <p>Nome de usuário: {{ user.name }}</p>

    <form @submit.prevent="saveConfig" class="config-form">
      <div class="form-group">
        <label>Salário Mensal</label>
        <input
          type="text"
          :value="salary"
          @input="salary = formatMoney($event.target.value)"
          placeholder="Ex: 3.200,50"
          required
        />
      </div>

      <div class="form-group">
        <label>Limite Máximo de Gastos</label>
        <input
          type="text"
          :value="limit"
          @input="limit = formatMoney($event.target.value)"
          placeholder="Ex: 1.500,00"
          required
        />
      </div>

      <div class="form-group">
        <label>Moeda</label>
        <select v-model="currency">
          <option value="BRL">R$ - Real</option>
          <option value="USD">$ - Dólar</option>
          <option value="EUR">€ - Euro</option>
        </select>
      </div>

      <button type="submit" class="save-btn">Salvar Configurações</button>
    </form>

    <p v-if="saved" class="saved-msg">Configurações salvas! 🎯</p>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Config",

  data() {
    return {
      salary: "",
      limit: "",
      currency: "BRL",
      saved: false,
      userId: null,
      user: {
        name: "",
      },
    };
  },

  mounted() {
    const userLS = JSON.parse(localStorage.getItem("user"));

    if (!userLS || !userLS.id) {
      console.error("Nenhum usuário logado!");
      return;
    }

    this.userId = userLS.id;

    axios
      .get(`http://localhost:8080/api/users/${this.userId}`)
      .then((response) => {
        const u = response.data;

        this.salary = u.salary
          ? this.formatMoney(u.salary.toString().replace(".", ","))
          : "";

        this.limit = u.limitValue
          ? this.formatMoney(u.limitValue.toString().replace(".", ","))
          : "";

        this.currency = u.currency ?? "BRL";
        this.user.name = u.name ?? "";
      })
      .catch((err) => {
        console.error("Erro ao carregar configurações:", err);
      });
  },

  methods: {
    // máscara BRL
    formatMoney(value) {
      if (!value) return "";

      let num = value.replace(/\D/g, "");

      if (!num) return "";

      num = (parseInt(num) / 100).toFixed(2);

      num = num.replace(".", ",");

      return num.replace(/\B(?=(\d{3})+(?!\d))/g, ".");
    },

    saveConfig() {
      if (!this.userId) {
        console.error("ID do usuário não encontrado!");
        return;
      }

      const normalize = (v) =>
        parseFloat(v.replace(/\./g, "").replace(",", "."));

      const payload = {
        salary: normalize(this.salary),
        limitValue: normalize(this.limit),
        currency: this.currency,
      };

      axios
        .put(`http://localhost:8080/api/users/${this.userId}/config`, payload)
        .then(() => {
          this.saved = true;
          setTimeout(() => (this.saved = false), 2000);
        })
        .catch((err) => {
          console.error("Erro ao salvar configurações:", err);
        });
    },
  },
};
</script>