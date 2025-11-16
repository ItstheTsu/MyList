<template>
  <div class="login-container">
    <h1>Login</h1>
    <form @submit.prevent="login">
      <div>
        <label>Email:</label>
        <input v-model="email" type="email" required />
      </div>
      <div>
        <label>Senha:</label>
        <input v-model="password" type="password" required />
      </div>
      <button type="submit">Entrar</button>
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>

    <div class="login-redirect">
      <p>Não tem uma conta?</p>
      <button @click="goToRegister">Cadastre-se</button>
    </div>
  </div>
</template>

<script>
import api from "../services/axios.js";

export default {
  name: "Login",
  data() {
    return { email: "", password: "", errorMessage: "" };
  },
  methods: {
    async login() {
      try {
        const response = await api.post("/auth/login", {
          email: this.email,
          password: this.password
        });
        const user = response.data;
        localStorage.setItem("user", JSON.stringify(user));
        this.$router.push("/dashboard");
      } catch (error) {
        console.error("Erro login:", error);
        this.errorMessage = "Email ou senha inválidos!";
      }
    },
    goToRegister() {
      this.$router.push("/register");
    },
  }
};
</script>