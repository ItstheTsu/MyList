<template>
  <div class="recuperar-senha-container">
    <h1>Recuperar Senha</h1>

    <!-- Se não tem token, mostra o form de pedir o link -->
    <form v-if="!token" @submit.prevent="sendRecoveryEmail">
      <input
        type="email"
        v-model="email"
        placeholder="Digite seu email"
        required
      />
      <button type="submit">Enviar link</button>
    </form>

    <!-- Se tem token, mostra o form de trocar a senha -->
    <form v-else @submit.prevent="resetPassword">
      <p>
        Email: <strong>{{ email }}</strong>
      </p>
      <input
        type="password"
        v-model="newPassword"
        placeholder="Nova senha"
        required
      />
      <input
        type="password"
        v-model="confirmPassword"
        placeholder="Confirme a nova senha"
        required
      />
      <button type="submit">Trocar Senha</button>
    </form>

    <!-- Mensagens -->
    <p v-if="message" class="success">{{ message }}</p>
    <p v-if="error" class="error">{{ error }}</p>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import api from "../services/api.js";

export default {
  setup() {
    const route = useRoute();
    const router = useRouter();

    const token = route.query.token || "";
    const email = ref("");
    const newPassword = ref("");
    const confirmPassword = ref("");
    const message = ref("");
    const error = ref("");

    onMounted(async () => {
      if (token) {
        try {
          const response = await api.get(`/auth/recover-password/${token}`);
          email.value = response.data.email;
        } catch (err) {
          error.value = "Token inválido ou expirado.";
        }
      }
    });

    const sendRecoveryEmail = async () => {
      try {
        const response = await api.post("/auth/recover-password", {
          email: email.value,
        });
        message.value = `Email de recuperação enviado! Verifique sua caixa de entrada.\nToken: ${response.data.token}`;
        error.value = "";
      } catch (err) {
        error.value = "Não foi possível enviar o email. Verifique o endereço.";
        message.value = "";
      }
    };

    const resetPassword = async () => {
      if (newPassword.value !== confirmPassword.value) {
        error.value = "As senhas não coincidem!";
        return;
      }

      try {
        await api.post(`/auth/recover-password/${token}`, {
          password: newPassword.value,
        });
        message.value = "Senha alterada com sucesso!";
        error.value = "";

        setTimeout(() => router.push("/login"), 1500);
      } catch (err) {
        error.value = err.response?.data?.message || "Erro ao redefinir senha.";
        message.value = "";
      }
    };

    return {
      token,
      email,
      newPassword,
      confirmPassword,
      message,
      error,
      sendRecoveryEmail,
      resetPassword,
    };
  },
};
</script>