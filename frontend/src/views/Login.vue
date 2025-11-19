<template class="page-full">
  <div class="login-container">
    <h1>Login</h1>
    <form @submit.prevent="login">
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
      <button type="submit">Entrar</button>

      <div class="register-redirect">
        <a @click="goToRegister">Não têm uma conta?</a>
      </div>
    </form>
    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
  </div>
</template>

<script>
import api from "../services/api.js";
import "../styles/LoginRegister/LoginRegister.css";

export default {
  name: "Login",
  data() {
    return { email: "", password: "", errorMessage: "" };
  },
  methods: {
    async login(event) {
      this.errorMessage = "";
      if (event) event.preventDefault();
      try {
        const response = await api.post("/auth/login", {
          email: this.email,
          password: this.password,
        });

        const user = response.data;
        const token = response.data.token;

        api.defaults.headers.Authorization = `Bearer ${token}`;
        localStorage.setItem("user", JSON.stringify(user));
        localStorage.setItem("token", token);
        this.$router.push("/dashboard");
      } catch (error) {
        console.error("Erro login:", error);

        if (error.response) {
          if (error.response.status === 401) {
            const msg = error.response.data;
            if (msg === "Usuário não encontrado") {
              this.errorMessage = "Conta não encontrada, realize o cadastro.";
            } else {
              this.errorMessage = "Email ou senha inválidos!";
            }
          } else {
            this.errorMessage = "Erro no servidor. Tente novamente mais tarde.";
          }
        } else {
          this.errorMessage = "Falha de rede. Verifique sua conexão.";
        }
      }
    },
    goToRegister() {
      this.$router.push("/register");
    },
  },
};
</script>