<template>
  <div class="register-container">
    <h1>Cadastro</h1>
    <form @submit.prevent="register">
      <div>
        <label>Nome:</label>
        <input v-model="name" type="text" required />
      </div>
      <div>
        <label>Email:</label>
        <input v-model="email" type="email" required />
      </div>
      <div>
        <label>Senha:</label>
        <input v-model="password" type="password" required />
      </div>
      <button type="submit">Cadastrar</button>
    </form>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>

    <div class="login-redirect">
      <p>Já tem uma conta?</p>
      <button @click="goToLogin">Entrar</button>
    </div>
  </div>
</template>

<script>
import api from "../services/axios.js";

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
        const user = response.data;

        localStorage.setItem("user", JSON.stringify(user));

        this.$router.push("/dashboard");
      } catch (error) {
        console.error("Erro no cadastro:", error);
        this.errorMessage = "Erro ao cadastrar usuário. Verifique os dados.";
      }
    },
    goToLogin() {
      this.$router.push("/login");
    },
  },
};
</script>
