<template class="page-full">
  <div class="register-container">
    <h1>Cadastro</h1>
    <form @submit.prevent="register">
      <div>
        <input v-model="name" type="text" placeholder="Nome" required />
      </div>
      <div>
        <input v-model="email" type="email" placeholder="Email" required />
      </div>
      <div>
        <input
          v-model="password"
          type="password"
          placeholder="Senha"
          required
        />
      </div>
      <button type="submit">Cadastrar</button>

      <div class="login-redirect">
        <a @click="goToLogin">Já tem uma conta?</a>
      </div>
    </form>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>
  </div>
</template>

<script>
import api from "../services/api.js";
import "../styles/LoginRegister/LoginRegister.css";

export default {
  name: "Register",
  data() {
    return {
      name: "",
      email: "",
      password: "",
      errorMessage: "",
      successMessage: "",
    };
  },
  methods: {
    async register() {
      try {
        const response = await api.post("/users", {
          name: this.name,
          email: this.email,
          password: this.password,
        });

        if (response.status === 201 || response.status === 200) {
          const user = response.data;
          const token = response.data.token;

          api.defaults.headers.Authorization = `Bearer ${token}`;
          localStorage.setItem("user", JSON.stringify(user));
          this.successMessage =
            "Cadastro realizado! Redirecionando para o Dashboard...";
          this.errorMessage = "";

          setTimeout(() => {
            this.$router.push("/dashboard");
          }, 1000);
        }
      } catch (err) {
        if (err.response && err.response.status === 409) {
          this.errorMessage = "Email já cadastrado. Tente outro.";
        } else {
          this.errorMessage = "Erro inesperado. Tente novamente.";
        }
        this.successMessage = "";
        console.error("Erro no cadastro:", err);
      }
    },

    goToLogin() {
      this.$router.push("/login");
    },
  },
};
</script>
